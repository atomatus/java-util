package com.atomatus.util.serializer;

import com.atomatus.util.Base64;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;

import java.io.Serializable;
import java.util.Objects;

final class SerializerImplBSON extends SerializerImplJSON {

    @Override
    public <T extends Serializable> String serialize(T t, String rootElement) {
        return Base64.getEncoder().encodeToString(serializeAsBytes(t, rootElement));
    }

    @Override
    public <T extends Serializable> byte[] serializeAsBytes(T t, String rootElement) {
        try{
            String json = super.serialize(t, rootElement);
            BsonDocument bson = BsonDocument.parse(json);
            BasicOutputBuffer outputBuffer = new BasicOutputBuffer();
            BsonBinaryWriter writer = new BsonBinaryWriter(outputBuffer);
            new BsonDocumentCodec().encode(writer, bson, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
            byte[] byteArr = outputBuffer.toByteArray();
            outputBuffer.close();
            return byteArr;
        } catch (SerializerException e) {
            throw e;
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> T deserialize(String serialized, String rootElement, Class<T> type) {
        try {
            byte[] bson = Base64.getDecoder().decode(Objects.requireNonNull(serialized));
            return deserialize(bson, rootElement, type);
        } catch (SerializerException e) {
            throw e;
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public <T extends Serializable> T deserialize(byte[] serialized, String rootElement, Class<T> type) {
        try {
            BsonDocument doc = new RawBsonDocument(serialized);
            return super.deserialize(doc.toJson(), rootElement, type);
        } catch (SerializerException e) {
            throw e;
        } catch (Throwable e) {
            throw new SerializerException(e);
        }
    }
}
