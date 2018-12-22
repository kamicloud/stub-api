<?php

namespace YetAnotherGenerator;

abstract class BaseMessage
{
    use ValueHelper;

    public function validateInput()
    {
        $this->validate($this->requestRules());
    }

    public function validateOutput()
    {
        $this->validate($this->responseRules());
    }

    abstract public function requestRules();

    abstract public function responseRules();

}
