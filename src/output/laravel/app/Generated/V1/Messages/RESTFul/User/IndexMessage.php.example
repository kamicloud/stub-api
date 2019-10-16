<?php

namespace App\Generated\V1\Messages\RESTFul\User;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\BOs\Enums\QuerySort;
use App\Generated\V1\DTOs\UserDTO;

class IndexMessage extends Message
{
    use ValueHelper;

    protected $page;
    protected $sort;
    protected $limit;
    protected $totalCount;
    protected $perPage;
    protected $models;
    protected $dtoClass = UserDTO::class;

    /**
     * @return int
     */
    public function getPage()
    {
        return $this->page;
    }

    /**
     * @return int
     */
    public function getLimit()
    {
        return $this->limit;
    }

    /**
     * 排序，注意要转下snake_case
     *
     * @return string
     */
    public function getSort()
    {
        return $this->sort;
    }

    public function requestRules()
    {
        return [
            ['page', 'page', 'bail|integer|nullable', Constants::INTEGER | Constants::OPTIONAL, null],
            ['sort', 'sort', QuerySort::class, Constants::ENUM | Constants::OPTIONAL, null],
            ['limit', 'limit', 'bail|integer|nullable', Constants::INTEGER | Constants::OPTIONAL, null],
        ];
    }

    public function responseRules()
    {
        return [
            ['totalCount', 'total_count', 'bail|integer', Constants::INTEGER, null],
            ['perPage', 'per_page', 'bail|integer', Constants::INTEGER, null],
            ['models', 'models', $this->dtoClass, Constants::MODEL | Constants::ARRAY, null],
        ];
    }

    public function setResponse($totalCount, $perPage, $models)
    {
        $this->perPage = $perPage;
        $this->totalCount = $totalCount;
        $this->models = $models;
    }

}

