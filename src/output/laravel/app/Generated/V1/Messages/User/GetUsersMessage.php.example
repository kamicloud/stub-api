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
     * @return Gender
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
            ['id', 'id', 'bail|integer', null, null],
            ['gender', 'gender', Gender::class, Constants::ENUM, null],
            ['page', 'page', 'bail|nullable|integer', Constants::OPTIONAL, null],
            ['testUser', 'testUser', UserDTO::class, Constants::OPTIONAL | Constants::MODEL, null],
            ['testUsers', 'testUsers', UserDTO::class, Constants::OPTIONAL | Constants::ARRAY | Constants::MODEL, null],
        ];
    }

    public function responseRules()
    {
        return [
            ['val', 'val', 'bail|string', null, null],
            ['user', 'user', UserDTO::class, Constants::MODEL, null],
        ];
    }

    public function setResponse($val, $user)
    {
        $this->val = $val;
        $this->user = $user;
    }

}
