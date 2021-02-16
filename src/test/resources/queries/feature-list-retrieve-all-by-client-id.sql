SELECT
    fl.ID_CLIENT ,
    fl.ID_ENTRY,
    fl.ID_FEATURECODE ,
    fl.ACTIVATION_DATE,
    fl.EXPIRATION_DATE,
    fc.CODENO,
    fc.CODETEXT,
    e.ID_PERSON,
    e_person.IDENTID,
    '' as GIVEN_NAME,
    '' as SURNAME,
    e_address.STREET,
    e_address.HOUSENUMBER,
    e_address.ZIPCODE,
    e_address.CITY,
    cast(e_address.ISONO as varchar(4)) as COUNTRY,
    e_mail.EMAIL,
    e_bank.BLZ,
    e_bankCon.ACCOUNTNUMBER,
    e_bankCon.IBAN,
    e_bank.BIC
FROM
    [MidasFeatureList].[dbo].[TFEATURELIST] fl with (nolock)
    join [MidasFeatureList].[dbo].[TFL_ENTRY] e with (nolock) on
        fl.ID_ENTRY = e.ID_ENTRY
    join [MidasFeatureList].[dbo].[TFEATURECODE] fc with (nolock) on
        fl.ID_FEATURECODE = fc.ID_FEATURECODE
    left outer join [MidasFeatureList].[dbo].[TFL_PERSON] e_person with (nolock) on
        e_person.ID_PERSON = e.ID_PERSON
    left outer join [MidasFeatureList].[dbo].[TFL_ADDRESS] e_address with (nolock) on
        e_address.ID_ADDRESS = e.ID_ADDRESS
    left outer join [MidasFeatureList].[dbo].[TFL_EMAIL] e_mail with (nolock) on
        e_mail.ID_EMAIL = e.ID_EMAIL
    left outer join [MidasFeatureList].[dbo].[TFL_BANKCONNECTION] e_bankCon with (nolock) on
        e_bankCon.ID_BANKCONNECTION = e.ID_BANKCONNECTION
    left outer join [MidasFeatureList].[dbo].[TFL_BANK] e_bank with (nolock) on
        e_bank.ID_BANK = e_bankCon.ID_BANK
WHERE
    (fl.ID_CLIENT = %s)