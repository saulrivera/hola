package outland.emr.tracking.utils;

import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import org.joda.time.DateTime;

public class MyDateTimeDeserializer extends DateTimeDeserializer{
    public MyDateTimeDeserializer() {
        // no arg constructor providing default values for super call
        super(DateTime.class, FormatConfig.DEFAULT_DATETIME_PARSER);
    }
}
