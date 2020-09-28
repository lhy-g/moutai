package com.tongchuang.general.core.constant;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class TimeFrameQuery {

    LocalDateTime startTime;

    LocalDateTime endTime;
}
