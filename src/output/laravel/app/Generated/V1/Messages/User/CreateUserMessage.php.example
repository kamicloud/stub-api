<?php

namespace App\Generated\V1\Messages\User;

use App\Generated\V1\Enums\Gender;
use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\Http\Messages\Message;
use Kamicloud\StubApi\Utils\Constants;
use App\Generated\V1\DTOs\UserDTO;

class CreateUserMessage extends Message
{
    use ValueHelper;

    protected $email;
    protected $emails;
    protected $gender;
    protected $genders;
    protected $id;
    protected $ids;
    protected $user;
    protected $users;

    /**
     * 查询的ID
     * @return string
     */
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * @return string[]
     */
    public function getEmails()
    {
        return $this->emails;
    }

    /**
     * @return Gender
     */
    public function getGender()
    {
        return $this->gender;
    }

    /**
     * @return Gender[]
     */
    public function getGenders()
    {
        return $this->genders;
    }

    /**
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return int[]
     */
    public function getIds()
    {
        return $this->ids;
    }

    public function requestRules()
    {
        return [
            ['email', 'email', 'bail|string', null, null],
            ['emails', 'emails', 'bail|string', Constants::ARRAY, null],
            ['gender', 'gender', Gender::class, Constants::ENUM, null],
            ['genders', 'genders', Gender::class, Constants::ARRAY | Constants::ENUM, null],
            ['id', 'id', 'bail|integer', null, null],
            ['ids', 'ids', 'bail|integer', Constants::ARRAY, null],
        ];
    }

    public function responseRules()
    {
        return [
            ['user', 'user', UserDTO::class, Constants::MODEL, null],
            ['users', 'users', UserDTO::class, Constants::ARRAY | Constants::MODEL, null],
        ];
    }

    public function setResponse($user, $users)
    {
        $this->user = $user;
        $this->users = $users;
    }

}
