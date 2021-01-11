package com.playground.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.model.StockTrade;
import com.playground.repository.StockTradeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StockTradeRestControllerTest {
    ObjectMapper om = new ObjectMapper();
    @Autowired
    StockTradeRepository stockTradeRepository;
    @Autowired
    MockMvc mockMvc;

    Map<String, StockTrade> testData;

    @Before
    public void setup() {
        stockTradeRepository.deleteAll();
        testData = getTestData();
    }

    @Test
    public void testTradeCreationWithValidData() throws Exception {
        //test type buy
        StockTrade expectedRecord = testData.get("user23_buy_ABX");
        assertEquals("buy", expectedRecord.getType());

        StockTrade actualRecord = om.readValue(mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), StockTrade.class);

        Assert.assertTrue(new ReflectionEquals(expectedRecord, "id").matches(actualRecord));
        assertEquals(true, stockTradeRepository.findById(actualRecord.getId()).isPresent());

        //test type sell
        expectedRecord = testData.get("user23_sell_AAC");
        assertEquals("sell", expectedRecord.getType());

        actualRecord = om.readValue(mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), StockTrade.class);

        Assert.assertTrue(new ReflectionEquals(expectedRecord, "id").matches(actualRecord));
        assertEquals(true, stockTradeRepository.findById(actualRecord.getId()).isPresent());

        //test invalid type
        expectedRecord.setType("Foo");
        mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //max shares test
        expectedRecord.setType("sell");
        expectedRecord.setShares(101);
        mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //min shares test
        expectedRecord.setType("sell");
        expectedRecord.setShares(0);
        mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetTrades() throws Exception {
        Map<String, StockTrade> testData = getTestData();

        Map<String, StockTrade> expectedMap = new HashMap<>();
        List<StockTrade> expected = new ArrayList<>();
        for (Map.Entry<String, StockTrade> kv : testData.entrySet()) {
            StockTrade response = om.readValue(mockMvc.perform(post("/trades")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), StockTrade.class);
            expectedMap.put(kv.getKey(), response);
        }
        Collections.sort(Arrays.asList(expectedMap.values().toArray(new StockTrade[testData.size()])), Comparator.comparing(StockTrade::getId));

        //without filter
        List<StockTrade> actualRecords = om.readValue(mockMvc.perform(get("/trades"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expected.get(i), "id").matches(actualRecords.get(i)));
        }

        //with type filter buy
        actualRecords = om.readValue(mockMvc.perform(get("/trades?type=buy"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        Assert.assertTrue(new ReflectionEquals(expectedMap.get("user23_buy_ABX"), "id").matches(actualRecords.get(0)));

        // with type filter sell
        expected = expectedMap.entrySet().stream().filter(kv -> kv.equals("user23_buy_ABX")).map(kv -> kv.getValue()).collect(
                Collectors.toList());
        Collections.sort(expected, Comparator.comparing(StockTrade::getId));

        actualRecords = om.readValue(mockMvc.perform(get("/trades?type=sell"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expected.get(i), "id").matches(actualRecords.get(i)));
        }

        //non existing type filter
        mockMvc.perform(get("/trades?type=boo"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(status().isOk());

        //with user filter
        expected = Arrays.asList(expectedMap.entrySet().stream().filter(kv -> "user23_buy_ABX,user23_sell_AAC".contains(kv.getKey())).collect(Collectors.toMap(kv -> kv.getKey(), kv -> kv.getValue())).values().toArray(new StockTrade[2]));
        Collections.sort(expected, Comparator.comparing(StockTrade::getId));

        actualRecords = om.readValue(mockMvc.perform(get("/trades?user_id=23"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expected.get(i), "id").matches(actualRecords.get(i)));
        }

        //non existing user filter
        mockMvc.perform(get("/trades?userId=" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(status().isOk());

        //test with user and type buy
        expected = Arrays.asList(expectedMap.entrySet().stream().filter(kv -> "user23_buy_ABX".contains(kv.getKey())).collect(Collectors.toMap(kv -> kv.getKey(), kv -> kv.getValue())).values().toArray(new StockTrade[1]));
        Collections.sort(expected, Comparator.comparing(StockTrade::getId));

        actualRecords = om.readValue(mockMvc.perform(get("/trades?user_id=23&type=buy"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expected.get(i), "id").matches(actualRecords.get(i)));
        }

        //test with user and type sell
        expected = Arrays.asList(expectedMap.entrySet().stream().filter(kv -> "user23_sell_AAC".contains(kv.getKey())).collect(Collectors.toMap(kv -> kv.getKey(), kv -> kv.getValue())).values().toArray(new StockTrade[1]));
        Collections.sort(expected, Comparator.comparing(StockTrade::getId));

        actualRecords = om.readValue(mockMvc.perform(get("/trades?user_id=23&type=sell"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<StockTrade>>() {
        });
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expected.get(i), "id").matches(actualRecords.get(i)));
        }

    }

    @Test
    public void testGetTradeRecordWithId() throws Exception {
        StockTrade expectedRecord = getTestData().get("user23_buy_ABX");
        expectedRecord = om.readValue(mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), StockTrade.class);

        //for existing
        StockTrade actualRecord = om.readValue(mockMvc.perform(get("/trades/" + expectedRecord.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), StockTrade.class);

        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        //non existing
        mockMvc.perform(get("/trades/" + Integer.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testNotAllowedMethod() throws Exception {
        StockTrade expectedRecord = getTestData().get("user23_buy_ABX");

        StockTrade actualRecord = om.readValue(mockMvc.perform(post("/trades")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), StockTrade.class);

        mockMvc.perform(put("/trades/" + actualRecord.getId()))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(patch("/trades/" + actualRecord.getId()))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(delete("/trades/" + actualRecord.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    private Map<String, StockTrade> getTestData() {
        Map<String, StockTrade> data = new HashMap<>();

        StockTrade user23_buy_ABX = new StockTrade(
                "buy",
                23,
                "ABX",
                30,
                134,
                1531522701000l);
        data.put("user23_buy_ABX", user23_buy_ABX);

        StockTrade user23_sell_AAC = new StockTrade(
                "sell",
                23,
                "AAC",
                12,
                133,
                1521522701000l);
        data.put("user23_sell_AAC", user23_sell_AAC);

        StockTrade user24_sell_AAC = new StockTrade(
                "sell",
                24,
                "AAC",
                12,
                133,
                1511522701000l);
        data.put("user24_sell_AAC", user24_sell_AAC);

        StockTrade user25_sell_AAC = new StockTrade(
                "sell",
                25,
                "AAC",
                12,
                111,
                1501522701000l);
        data.put("user25_sell_AAC", user25_sell_AAC);


        return data;
    }
}
