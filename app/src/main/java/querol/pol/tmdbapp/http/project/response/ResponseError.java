package querol.pol.tmdbapp.http.project.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class ResponseError extends Response{

    @SerializedName(value="status_message", alternate={"accessToken","message"}) private String error;

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "httpStatus=" + httpStatus +
                ", error='" + error + '\'' +
                '}';
    }
}
