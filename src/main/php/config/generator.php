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

];
