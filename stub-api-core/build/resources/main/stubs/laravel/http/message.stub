<?php

namespace App\Generated\V1\Messages\Article;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\ArticleDTO;

class CreateArticleMessage extends Message
{
    use ValueHelper;

    protected $title;
    protected $content;
    protected $article;

    /**
     * @return string
     */
    public function getTitle()
    {
        return $this->title;
    }

    /**
     * @return string
     */
    public function getContent()
    {
        return $this->content;
    }

    public function requestRules()
    {
        return [
            ['title', 'title', 'bail|string', Constants::STRING, null],
            ['content', 'content', 'bail|string', Constants::STRING, null],
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
