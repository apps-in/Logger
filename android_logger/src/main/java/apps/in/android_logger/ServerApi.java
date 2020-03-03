package apps.in.android_logger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerApi {

    @POST("Api/Log/PostLog")
    Call<Void> sendLog(@Body LogItem logItem);

}
