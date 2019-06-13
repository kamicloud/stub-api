<?php

namespace App\Generated\V1\Messages\User;

use App\Generated\V1\Enums\Gender;
use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\UserDTO;

class GetUsersMessage extends Message
{
    use ValueHelper;

    protected $id;
    protected $gender;
    protected $page;
    protected $testUser;
    protected $testUsers;
    protected $val;
    protected $user;

    /**
     * 查询的ID
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return mixed
     */
    public function getGender()
    {
        return $this->gender;
    }

    /**
     * @return int|null
     */
    public function getPage()
    {
        return $this->page;
    }

    /**
     * @return UserDTO|null
     */
    public function getTestUser()
    {
        return $this->testUser;
    }

    /**
     * @return UserDTO[]|null
     */
    public function getTestUsers()
    {
        return $this->testUsers;
    }

    public function requestRules()
    {
        return [
            ['id', 'id', 'bail|integer', Constants::INTEGER, null],
            ['gender', 'gender', Gender::class, Constants::ENUM, null],
            ['page', 'page', 'bail|nullable|integer', Constants::INTEGER | Constants::OPTIONAL, null],
            ['testUser', 'testUser', UserDTO::class, Constants::MODEL | Constants::OPTIONAL, null],
            ['testUsers', 'testUsers', UserDTO::class, Constants::MODEL | Constants::OPTIONAL | Constants::ARRAY, null],
        ];
    }

    public function responseRules()
    {
        return [
            ['val', 'val', 'bail|string', Constants::STRING, null],
            ['user', 'user', UserDTO::class, Constants::MODEL, null],
        ];
    }

    public function setResponse($val, $user)
    {
        $this->val = $val;
        $this->user = $user;
    }

}
