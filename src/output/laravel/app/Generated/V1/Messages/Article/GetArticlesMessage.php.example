<?php

namespace App\Generated\V1\Messages\Article;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\ArticleDTO;

class GetArticlesMessage extends Message
{
    use ValueHelper;

    protected $articles;

    public function requestRules()
    {
        return [
        ];
    }

    public function responseRules()
    {
        return [
            ['articles', 'articles', ArticleDTO::class, Constants::MODEL | Constants::ARRAY, null],
        ];
    }

    public function setResponse($articles)
    {
        $this->articles = $articles;
    }

}
