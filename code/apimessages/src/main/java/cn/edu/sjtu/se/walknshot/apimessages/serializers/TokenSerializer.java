package cn.edu.sjtu.se.walknshot.apimessages.serializers;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class TokenSerializer extends StdSerializer<Token> {
    public TokenSerializer() {
        this(null);
    }

    public TokenSerializer(Class<Token> t) {
        super(t);
    }

    @Override
    public void serialize(
            Token token, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeString(token.toString());
    }
}
