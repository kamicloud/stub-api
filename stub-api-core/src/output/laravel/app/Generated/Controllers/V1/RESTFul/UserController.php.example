<?php

namespace App\Generated\Controllers\V1\RESTFul;

use App\Http\Controllers\Controller;
use App\Generated\V1\Messages\RESTFul\User\IndexMessage;
use App\Generated\V1\Messages\RESTFul\User\ShowMessage;
use App\Generated\V1\Messages\RESTFul\User\StoreMessage;
use App\Generated\V1\Messages\RESTFul\User\UpdateMessage;
use App\Generated\V1\Messages\RESTFul\User\DestroyMessage;
use App\Http\Services\V1\RESTFul\UserService;
use DB;

class UserController extends Controller
{
    public $handler;

    public function __construct(UserService $handler)
    {
        $this->handler = $handler;
    }

    public function index(IndexMessage $message)
    {
        $message->validateInput();
        $this->handler->index($message);
        $message->validateOutput();
        return $message->getResponse();
    }

    public function show(ShowMessage $message, $id)
    {
        $message->validateInput();
        $this->handler->show($message, $id);
        $message->validateOutput();
        return $message->getResponse();
    }

    public function store(StoreMessage $message)
    {
        return DB::transaction(function () use ($message) {
            $message->validateInput();
            $this->handler->store($message);
            $message->validateOutput();
            return $message->getResponse();
        });
    }

    public function update(UpdateMessage $message, $id)
    {
        return DB::transaction(function () use ($message, $id) {
            $message->validateInput();
            $this->handler->update($message, $id);
            $message->validateOutput();
            return $message->getResponse();
        });
    }

    public function destroy(DestroyMessage $message, $id)
    {
        return DB::transaction(function () use ($message, $id) {
            $message->validateInput();
            $this->handler->destroy($message, $id);
            $message->validateOutput();
            return $message->getResponse();
        });
    }

}

