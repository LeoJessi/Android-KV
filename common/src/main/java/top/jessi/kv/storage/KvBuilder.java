package top.jessi.kv.storage;

import android.content.Context;

import com.google.gson.Gson;

public class KvBuilder {

    private final Context context;
    private Storage cryptoStorage;
    private Converter converter;
    private Parser parser;
    private Encryption encryption;
    private Serializer serializer;
    private LogInterceptor logInterceptor;

    public KvBuilder(Context context) {
        KvUtils.checkNull("Context", context);
        this.context = context.getApplicationContext();
    }

    public KvBuilder setStorage(Storage storage) {
        this.cryptoStorage = storage;
        return this;
    }

    public KvBuilder setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    public KvBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public KvBuilder setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
        return this;
    }

    public KvBuilder setConverter(Converter converter) {
        this.converter = converter;
        return this;
    }

    public KvBuilder setEncryption(Encryption encryption) {
        this.encryption = encryption;
        return this;
    }

    LogInterceptor getLogInterceptor() {
        if (logInterceptor == null) {
            logInterceptor = message -> {
                //empty implementation
            };
        }
        return logInterceptor;
    }

    Storage getStorage() {
        if (cryptoStorage == null) {
            cryptoStorage = new SharedPreferencesStorage(context);
        }
        return cryptoStorage;
    }

    Converter getConverter() {
        if (converter == null) {
            converter = new KvConverter(getParser());
        }
        return converter;
    }

    Parser getParser() {
        if (parser == null) {
            parser = new GsonParser(new Gson());
        }
        return parser;
    }

    Encryption getEncryption() {
        if (encryption == null) {
            encryption = new ConcealEncryption();
            if (!encryption.init()) {
                encryption = new NoEncryption();
            }
        }
        return encryption;
    }

    Serializer getSerializer() {
        if (serializer == null) {
            serializer = new KvSerializer(getLogInterceptor());
        }
        return serializer;
    }

    public void build() {
        KV.build(this);
    }
}
