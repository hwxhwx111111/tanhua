package com.itheima.tanhua.api.mongo;

import java.util.List;

public interface UserLocationServiceApi {


    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    List<Long> queryNearUser(Long id, Double valueOf);
}
