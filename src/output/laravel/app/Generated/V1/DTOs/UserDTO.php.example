<?php

namespace App\Generated\V1\DTOs;

use Kamicloud\StubApi\Concerns\ValueHelper;
use Kamicloud\StubApi\DTOs\DTO;
use Kamicloud\StubApi\Utils\Constants;

class UserDTO extends DTO
{
    use ValueHelper;

    protected $name;
    protected $id;
    protected $avatar;

    /**
     * 这里只是留了一个备注
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * 一个注释
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return string
     */
    public function getAvatar()
    {
        return $this->avatar;
    }

    public function setName($name)
    {
        $this->name = $name;
    }

    public function setId($id)
    {
        $this->id = $id;
    }

    public function setAvatar($avatar)
    {
        $this->avatar = $avatar;
    }

    public function getAttributeMap()
    {
        return [
            ['name', 'name', 'bail|string', null, null],
            ['id', 'id', 'bail|integer', Constants::MUTABLE, null],
            ['avatar', 'avatar', 'bail|string', null, null],
        ];
    }

}
