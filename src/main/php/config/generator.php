<?php

return [
    'exceptions' => [

        'invalid-parameter-exception' => Kamicloud\StubApi\Exceptions\InvalidParameterException::class,

        'server-internal-error' => Kamicloud\StubApi\Exceptions\ServerInternalErrorException::class,

        'maintain-mode-exception' => Kamicloud\StubApi\Exceptions\MaintainModeException::class,

    ],

    'values' => [
        'success-status' => 0,
        'success-message' => 'message',
    ],

    /**
     * 默认时间的传输格式为字符串
     *
     * 开启后传输格式为整型
     */
    'request-date-timestamp' => false,
    'response-date-timestamp' => false,
];
