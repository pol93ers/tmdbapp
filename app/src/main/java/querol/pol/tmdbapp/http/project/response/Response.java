package querol.pol.tmdbapp.http.project.response;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public abstract class Response implements Serializable{
    @Expose(serialize = false, deserialize = false) protected String requestId;
    @Expose(serialize = false, deserialize = false) protected int httpStatus;

    public void setHttpStatus(okhttp3.Response response) {
        this.httpStatus = response.code();
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "Response{" +
                "httpStatus=" + httpStatus +
                '}';
    }
}
