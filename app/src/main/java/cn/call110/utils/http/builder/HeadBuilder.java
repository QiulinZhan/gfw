package cn.call110.utils.http.builder;

import cn.call110.utils.http.OkHttpUtils;
import cn.call110.utils.http.request.OtherRequest;
import cn.call110.utils.http.request.RequestCall;

public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
