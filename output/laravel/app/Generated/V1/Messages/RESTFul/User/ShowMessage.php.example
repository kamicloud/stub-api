<?php

namespace App\Generated\V1\Messages\RESTFul\User;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\UserDTO;

class ShowMessage extends Message
{
    use ValueHelper;

    protected $model;
    protected $dtoClass = UserDTO::class;

    public function requestRules()
    {
        return [
        ];
    }

    public function responseRules()
    {
        return [
            ['model', 'model', $this->dtoClass, Constants::MODEL, null],
        ];
    }

    public function setResponse($model)
    {
        $this->model = $model;
    }

}

