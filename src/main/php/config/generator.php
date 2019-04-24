<?php

return [
    'exceptions' => [

        'invalid-parameter-exception' => YetAnotherGenerator\Exceptions\InvalidParameterException::class,

        'server-internal-error' => YetAnotherGenerator\Exceptions\ServerInternalErrorException::class,

        'maintain-mode-exception' => YetAnotherGenerator\Exceptions\MaintainModeException::class,

    ],

    'values' => [
        'success-status' => 0,
        'success-message' => 'message',
    ],

];
