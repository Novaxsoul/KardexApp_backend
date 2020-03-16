package kardexapi.entities;

import org.apache.johnzon.mapper.Converter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;

// Clase para la serialización y deserealización de las fechas
public class LocalDateConverter implements Converter<java.sql.Date> {
    @Override
    public String toString(final java.sql.Date instance) {
        final Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(instance);
        return DatatypeConverter.printDateTime(cal);
    }

    @Override
    public java.sql.Date fromString(final String text) {
        return (java.sql.Date) DatatypeConverter.parseDateTime(text).getTime();
    }
}