package org.switchyard.component.soap;

import org.switchyard.HandlerException;
import org.switchyard.component.soap.InboundHandler;
import org.switchyard.transform.Transformer;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base {@link HandlerException} to SOAP fault transformer.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HandlerExceptionTransformer implements Transformer {

    @Override
    public Class getFromType() {
        return HandlerException.class;
    }

    @Override
    public Class getToType() {
        return String.class;
    }

    @Override
    public String getFrom() {
        return HandlerException.MESSAGE_TYPE;
    }

    @Override
    public String getTo() {
        return InboundHandler.SOAP_FAULT_MESSAGE_TYPE;
    }

    @Override
    public Object transform(Object from) {
        HandlerException e = (HandlerException) from;
        StringWriter stackTraceWriter = new StringWriter();

        e.printStackTrace(new PrintWriter(stackTraceWriter));
        return "<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <faultcode>soap:Server.AppError</faultcode>\n" +
                "   <faultstring>" + e.getMessage() + "</faultstring>\n" +
                "   <detail>\n" +
                "      <message>" + e.getMessage() + "</message>\n" +
                "   </detail>\n" +
                "</soap:Fault>";
    }
}
