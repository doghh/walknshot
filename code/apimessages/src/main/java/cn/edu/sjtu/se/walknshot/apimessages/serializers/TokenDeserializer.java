package cn.edu.sjtu.se.walknshot.apimessages.serializers;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class TokenDeserializer extends StdDeserializer<Token> {
    public TokenDeserializer() {
        this(null);
    }

    public TokenDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Token deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        JsonNode node =  jp.getCodec().readTree(jp);
        if (node.isNull())
            return null;
        String s = node.textValue();
        Token token = Token.fromString(s);
        if (token == null)
            throw ctx.weirdStringException(s, Token.class, "Bad token format");
        return token;
    }
}
