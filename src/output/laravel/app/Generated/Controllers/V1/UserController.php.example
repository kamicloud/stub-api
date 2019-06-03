<?php

namespace App\Generated\Controllers\V1;

use App\Generated\V1\Messages\User\GetUsersMessage;
use App\Generated\V1\Messages\User\CreateUserMessage;
use App\Http\Controllers\Controller;
use App\Http\Services\V1\UserService;
use DB;

class UserController extends Controller
{
    public $handler;

    public function __construct(UserService $handler)
    {
        $this->handler = $handler;
    }

    public function createUser(CreateUserMessage $message)
    {
        $message->validateInput();
        $this->handler->createUser($message);
        $message->validateOutput();
        return $message->getResponse();
    }

    public function getUsers(GetUsersMessage $message)
    {
        $message->validateInput();
        $this->handler->getUsers($message);
        $message->validateOutput();
        return $message->getResponse();
    }

}
