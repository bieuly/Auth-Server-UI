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
    @Secure(false)
    @TemplateName("tokenRequest")
    @Http(method = HttpMethod.POST, uri = "/token", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> tokenRequest(@Content String str);
    
    @Secure(false)
    @TemplateName("tokenVerify")
    @Http(method = HttpMethod.POST, uri = "/token/decoded.json", headers={
            @Header(name = "Content-Type", value = "text/plain")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> tokenVerify(@Content String str);
    
    @Secure(true)
    @TemplateName("getCustomers")
    @Http(method = HttpMethod.GET, uri = "/customers", headers={
            @Header(name = "Content-Type", value = "text/plain")
    })
//    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> getCustomers();
    
    @Secure(true)
    @TemplateName("getRoles")
    @Http(method = HttpMethod.GET, uri = "/roles/{customerId}", headers={
            @Header(name = "Content-Type", value = "text/plain")
    })
//    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> getRoles(@Var ("customerId") String customerId);
    
    @Secure(true)
    @TemplateName("getUsers")
    @Http(method = HttpMethod.GET, uri = "/users/{customerId}", headers={
            @Header(name = "Content-Type", value = "text/plain")
    })
//    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> getUsers(@Var ("customerId") String customerId);
    
    @Secure(true)
    @TemplateName("getPermissions")
    @Http(method = HttpMethod.GET, uri = "/permissions", headers={
            @Header(name = "Content-Type", value = "text/plain")
    })
//    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> getPermissions();
    
    @Secure(true)
    @TemplateName("createCustomer")
    @Http(method = HttpMethod.POST, uri = "/customers", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> createCustomer(@Content String str);
    
    @Secure(true)
    @TemplateName("createUser")
    @Http(method = HttpMethod.POST, uri = "/users", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> createUser(@Content String str);
    
    @Secure(true)
    @TemplateName("createRole")
    @Http(method = HttpMethod.POST, uri = "/roles", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> createRole(@Content String str);
    
    @Secure(false)
    @TemplateName("changePassword")
    @Http(method = HttpMethod.PUT, uri = "/users/{username}/password", headers={
            @Header(name = "Content-Type", value = "application/json")
    })
    @ContentTransformerClass(StringTransformer.class)
    @Hystrix(validator = HttpStatusValidator.class)
    RibbonRequest<ByteBuf> changePassword(@Content String str, @Var ("username") String username);
    
    
}