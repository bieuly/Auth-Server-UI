package util;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import com.netflix.ribbon.ServerError;
import com.netflix.ribbon.UnsuccessfulResponseException;
import com.netflix.ribbon.http.HttpResponseValidator;

public class AuthServerStatusValidator implements HttpResponseValidator {
	@Override
    public void validate(HttpClientResponse<ByteBuf> response) throws UnsuccessfulResponseException, ServerError {
        if (response.getStatus().code() / 100 != 2) {
            throw new UnsuccessfulResponseException("Unexpected HTTP status code " + response.getStatus());
        }
    }
}
