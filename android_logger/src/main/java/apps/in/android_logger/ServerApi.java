package apps.in.android_logger;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerApi {

    @POST("Api/PostLog")
    void sendLog(@Body LogItem logItem);

}
