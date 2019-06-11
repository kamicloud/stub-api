<?php

namespace App\Generated\Controllers\V1;

use App\Http\Services\V1\ArticleService;
use App\Generated\V1\Messages\Article\GetArticlesMessage;
use App\Http\Controllers\Controller;
use App\Generated\V1\Messages\Article\CreateArticleMessage;
use App\Generated\V1\Messages\Article\GetArticleMessage;
use App\Generated\V1\Messages\Article\GetArticleCommentsMessage;
use DB;

class ArticleController extends Controller
{
    public $handler;

    public function __construct(ArticleService $handler)
    {
        $this->handler = $handler;
    }

    public function getArticleComments(GetArticleCommentsMessage $message)
    {
        $message->validateInput();
        $this->handler->getArticleComments($message);
        $message->validateOutput();
        return $message->getResponse();
    }

    public function createArticle(CreateArticleMessage $message)
    {
        return DB::transaction(function () use ($message) {
            $message->validateInput();
            $this->handler->createArticle($message);
            $message->validateOutput();
            return $message->getResponse();
        });
    }

    public function getArticle(GetArticleMessage $message)
    {
        $message->validateInput();
        $this->handler->getArticle($message);
        $message->validateOutput();
        return $message->getResponse();
    }

    public function getArticles(GetArticlesMessage $message)
    {
        $message->validateInput();
        $this->handler->getArticles($message);
        $message->validateOutput();
        return $message->getResponse();
    }

}
