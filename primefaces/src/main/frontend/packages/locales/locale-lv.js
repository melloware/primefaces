import { lv } from "primelocale/js/lv.js";

if (window.PrimeFaces) {
  /** Latvian */
  PrimeFaces.locales["lv"] = lv;

  // custom PF labels
  PrimeFaces.locales["lv"] = $.extend(true, {}, PrimeFaces.locales["lv"], {
    isRTL: false,
    yearSuffix: "",
    timeOnlyTitle: "Tikai laiks",
    timeText: "Laiks",
    hourText: "Stunda",
    minuteText: "Minūte",
    secondText: "Sekunde",
    millisecondText: "Millisekunde",
    month: "Mēnesis",
    week: "Nedēļa",
    day: "Diena",
    allDayText: "Visa Diena",
    moreLinkText: "Vairāk...",
    noEventsText: "Nav notikumu",
    aria: {
      "datatable.sort.ASC": "aktivizēt, lai kārtotu kolonnu augošā secībā",
      "datatable.sort.DESC": "aktivizēt, lai kārtotu kolonnu dilstošā secībā",
      "colorpicker.OPEN": "Atvērt krāsu atlasītāju",
      "colorpicker.CLOSE": "Aizvērt krāsu atlasītāju",
      "colorpicker.CLEAR": "Notīrīt atlasīto krāsu",
      "colorpicker.MARKER": "Piesātinājums: {s}. Spilgtums: {v}.",
      "colorpicker.HUESLIDER": "Krāsu toņa slīdnis",
      "colorpicker.ALPHASLIDER": "Necaurspīdības slīdnis",
      "colorpicker.INPUT": "Krāsas vērtības lauks",
      "colorpicker.FORMAT": "Krāsu formāts",
      "colorpicker.SWATCH": "Krāsu palete",
      "colorpicker.INSTRUCTION": "Piesātinājuma un spilgtuma atlasītājs. Lai atlasītu, izmantojiet augšup, lejup, pa kreisi un pa labi vērsto bulttaustiņu.",
      "spinner.INCREASE": "Palielināt vērtību",
      "spinner.DECREASE": "Samazināt vērtību",
      "switch.ON": "Uz",
      "switch.OFF": "Izslēgts",
      "messages.ERROR": "Kļūda",
      "messages.FATAL": "Fatāla kļūda",
      "messages.INFO": "Informācija",
      "messages.WARN": "Brīdinājums",
    },
    messages: {
      //optional for Client Side Validation
      "jakarta.faces.component.UIInput.REQUIRED": "{0}: Validācijas kļūda: nepieciešama vērtība.",
      "jakarta.faces.converter.IntegerConverter.INTEGER": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem.",
      "jakarta.faces.converter.IntegerConverter.INTEGER_detail": "{2}: '{0}' jābūt skaitlim no -2147483648 līdz 2147483647. Piemērs: {1}.",
      "jakarta.faces.converter.DoubleConverter.DOUBLE": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem.",
      "jakarta.faces.converter.DoubleConverter.DOUBLE_detail": "{2}: '{0}' jābūt skaitlim starp 4.9E-324 un 1.7976931348623157E308. Piemērs: {1}.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL": "{2}: '{0}' jābūt decimāldaļskaitlim ar zīmi.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL_detail": "{2}: '{0}' jābūt decimāldaļskaitlim ar zīmi, kas sastāv no nulles vai vairāk cipariem, aiz kura var būt komats un decimāldaļa. Piemērs: {1}.",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem.",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER_detail": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem. Piemērs: {1}.",
      "jakarta.faces.converter.ByteConverter.BYTE": "{2}: '{0}' jābūt skaitlim no 0 līdz 255.",
      "jakarta.faces.converter.ByteConverter.BYTE_detail": "{2}: '{0}' jābūt skaitlim no 0 līdz 255. Piemērs: {1}.",
      "jakarta.faces.converter.CharacterConverter.CHARACTER": "{1}: '{0}' jābūt derīgai rakstzīmei.",
      "jakarta.faces.converter.CharacterConverter.CHARACTER_detail": "{1}: '{0}' jābūt derīgai ASCII rakstzīmei.",
      "jakarta.faces.converter.ShortConverter.SHORT": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem.",
      "jakarta.faces.converter.ShortConverter.SHORT_detail": "{2}: '{0}' jābūt skaitlim no -32768 līdz 32767. Piemērs: {1}.",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN": "{1}: '{0}' jābūt 'true' vai 'false'.",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN_detail": "{1}: '{0}' jābūt 'true' vai 'false'. Jebkura vērtība, kas nav 'true' tiks novērtēta kā 'false'.",
      "jakarta.faces.validator.LongRangeValidator.MAXIMUM": "{1}: Validācijas kļūda: vērtība ir lielāka par pieļaujamo maksimālo vērtību '{0}'.",
      "jakarta.faces.validator.LongRangeValidator.MINIMUM": "{1}: Validācijas kļūda: vērtība ir mazāka par pieļaujamo minimālo vērtību '{0}'.",
      "jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE": "{2}: Validācijas kļūda: norādītais atribūts nav starp paredzētajām vērtībām {0} un {1}.",
      "jakarta.faces.validator.LongRangeValidator.TYPE": "{0}: Validācijas kļūda: vērtības tips nav pareizs.",
      "jakarta.faces.validator.DoubleRangeValidator.MAXIMUM": "{1}: Validācijas kļūda: vērtība ir lielāka par pieļaujamo maksimālo vērtību '{0}'.",
      "jakarta.faces.validator.DoubleRangeValidator.MINIMUM": "{1}: Validācijas kļūda: vērtība ir mazāka par pieļaujamo minimālo vērtību '{0}'.",
      "jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE": "{2}: Validācijas kļūda: norādītais atribūts nav starp paredzētajām vērtībām {0} un {1}.",
      "jakarta.faces.validator.DoubleRangeValidator.TYPE": "{0}: Validācijas kļūda: vērtības tips nav pareizs.",
      "jakarta.faces.converter.FloatConverter.FLOAT": "{2}: '{0}' jābūt skaitlim, kas sastāv no viena vai vairākiem cipariem.",
      "jakarta.faces.converter.FloatConverter.FLOAT_detail": "{2}: '{0}' jābūt skaitlim starp 1.4E-45 un 3.4028235E38. Piemērs: {1}.",
      "jakarta.faces.converter.DateTimeConverter.DATE": "{2}: '{0}' nevar saprast kā datumu.",
      "jakarta.faces.converter.DateTimeConverter.DATE_detail": "{2}: '{0}'nevar saprast kā datumu. Piemērs: {1}.",
      "jakarta.faces.converter.DateTimeConverter.TIME": "{2}: '{0}' nevar saprast kā laiku.",
      "jakarta.faces.converter.DateTimeConverter.TIME_detail": "{2}: '{0}' nevar saprast kā laiku. Piemērs: {1}.",
      "jakarta.faces.converter.DateTimeConverter.DATETIME": "{2}: '{0}' nevar saprast kā datumu un laiku.",
      "jakarta.faces.converter.DateTimeConverter.DATETIME_detail": "{2}: '{0}' nevar saprast kā datumu un laiku. Piemērs: {1}.",
      "jakarta.faces.converter.DateTimeConverter.PATTERN_TYPE": "{1}: Lai pārvērstu vērtību '{0}', ir jānorāda atribūts 'pattern' vai 'type'.",
      "jakarta.faces.converter.NumberConverter.CURRENCY": "{2}: '{0}' nevar saprast kā valūtas vērtību.",
      "jakarta.faces.converter.NumberConverter.CURRENCY_detail": "{2}: '{0}' nevar saprast kā valūtas vērtību. Piemērs: {1}.",
      "jakarta.faces.converter.NumberConverter.PERCENT": "{2}: '{0}' nevar saprast kā procentu.",
      "jakarta.faces.converter.NumberConverter.PERCENT_detail": "{2}: '{0}' nevar saprast kā procentu. Piemērs: {1}.",
      "jakarta.faces.converter.NumberConverter.NUMBER": "{2}: '{0}' nevar saprast kā skaitli.",
      "jakarta.faces.converter.NumberConverter.NUMBER_detail": "{2}: '{0}' nevar saprast kā skaitli. Piemērs: {1}.",
      "jakarta.faces.converter.NumberConverter.PATTERN": "{2}: '{0}' nevar saprast kā skaitļu modeli.",
      "jakarta.faces.converter.NumberConverter.PATTERN_detail": "{2}: '{0}' nevar saprast kā skaitļu modeli. Piemērs: {1}.",
      "jakarta.faces.validator.LengthValidator.MINIMUM": "{1}: Validācijas kļūda: garums ir mazāks par pieļaujamo minimālo vērtību '{0}'.",
      "jakarta.faces.validator.LengthValidator.MAXIMUM": "{1}: Validācijas kļūda: garums ir lielāks par pieļaujamo maksimālo vērtību '{0}'.",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET": "Jāiestata regex modelis.",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET_detail": "Ir jāiestata regex modelis uz vērtību, kas nav tukša.",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED": "Regex modelis neatbilst.",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED_detail": "Regex modelis '{0}' neatbilst.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION": "Kļūda regulārajā izteiksmē.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION_detail": "Kļūda regulārajā izteiksmē, '{0}'.",
      "primefaces.FileValidator.FILE_LIMIT": "Pārsniegts maksimālais failu skaits.",
      "primefaces.FileValidator.FILE_LIMIT_detail": "Maksimālais skaits: {0}.",
      "primefaces.FileValidator.ALLOW_TYPES": "Nederīgs faila tips.",
      "primefaces.FileValidator.ALLOW_TYPES_detail": "Nederīgs faila tips: '{0}'. Atļautie veidi: '{1}'.",
      "primefaces.FileValidator.SIZE_LIMIT": "Nederīgs faila lielums.",
      "primefaces.FileValidator.SIZE_LIMIT_detail": "Fails '{0}' nedrīkst būt lielāks par {1}.",
      //optional for bean validation integration in client side validation
      "jakarta.faces.validator.BeanValidator.MESSAGE": "{1}: {0}",
      "jakarta.validation.constraints.AssertFalse.message": "jābūt false.",
      "jakarta.validation.constraints.AssertTrue.message": "jābūt true.",
      "jakarta.validation.constraints.DecimalMax.message": "jābūt mazākam vai vienādam ar {0}.",
      "jakarta.validation.constraints.DecimalMin.message": "jābūt lielākam vai vienādam ar {0}.",
      "jakarta.validation.constraints.Digits.message": "skaitliskā vērtība ārpus robežām (paredzēts &lt;{0} cipari&gt;.&lt;{1} cipari&gt;).",
      "jakarta.validation.constraints.Email.message": "jābūt pareizi izveidotai e-pasta adresei",
      "jakarta.validation.constraints.Future.message": "jābūt nākotnē.",
      "jakarta.validation.constraints.FutureOrPresent.message": "jābūt datumam tagadnē vai nākotnē",
      "jakarta.validation.constraints.Max.message": "jābūt mazākam vai vienādam ar {0}.",
      "jakarta.validation.constraints.Min.message": "jābūt lielākam vai vienādam ar {0}.",
      "jakarta.validation.constraints.Negative.message": "jābūt mazākam par 0",
      "jakarta.validation.constraints.NegativeOrZero.message": "jābūt mazākam vai vienādam ar 0",
      "jakarta.validation.constraints.NotBlank.message": "nedrīkst būt Null.",
      "jakarta.validation.constraints.NotEmpty.message": "nedrīkst būt Null.",
      "jakarta.validation.constraints.NotNull.message": "nedrīkst būt Null.",
      "jakarta.validation.constraints.Null.message": "jābūt Null.",
      "jakarta.validation.constraints.Past.message": "jābūt pagātnē.",
      "jakarta.validation.constraints.PastOrPresent.message": "datumam ir jābūt pagātnē vai tagadnē",
      "jakarta.validation.constraints.Pattern.message": 'jāatbilst "{0}".',
      "jakarta.validation.constraints.Positive.message": "jābūt lielākam par 0",
      "jakarta.validation.constraints.PositiveOrZero.message": "jābūt lielākam vai vienādam ar 0",
      "jakarta.validation.constraints.Size.message": "lielumam jābūt starp {0} un {1}.",
    },
  });
}
