package services;

import com.digitalpaytech.client.util.HttpStatusValidator;

import com.digitalpaytech.ribbon.annotation.Secure;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Content;
import com.netflix.ribbon.proxy.annotation.ContentTransformerClass;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Http.Header;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.netflix.ribbon.proxy.annotation.ResourceGroup;
import com.netflix.ribbon.proxy.annotation.TemplateName;
import com.netflix.ribbon.proxy.annotation.Var;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.channel.StringTransformer;

@ResourceGroup(name = "authserver")
public interface AuthClient {
    @Secure(true)
    @TemplateName("tokenRequest")
    @Http(method = HttpMethod.POST, uri = "/token", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> tokenRequest(@Content String str);
}