<?php

namespace YetAnotherGenerator;

abstract class BaseMessage
{
    use ValueHelper;

    public function validateInput()
    {
        $this->validateAttributes($this->requestRules());
    }

    public function validateOutput()
    {
        $this->validateAttributes($this->responseRules());
    }

    abstract public function requestRules();

    abstract public function responseRules();

}
