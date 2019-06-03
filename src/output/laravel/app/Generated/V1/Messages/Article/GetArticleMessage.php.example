<?php

namespace App\Generated\V1\Messages\Article;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\ArticleDTO;

class GetArticleMessage extends Message
{
    use ValueHelper;

    protected $id;
    protected $article;

    /**
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    public function requestRules()
    {
        return [
            ['id', 'id', 'bail|integer', Constants::INTEGER, null],
        ];
    }

    public function responseRules()
    {
        return [
            ['article', 'article', ArticleDTO::class, Constants::MODEL, null],
        ];
    }

    public function setResponse($article)
    {
        $this->article = $article;
    }

}
