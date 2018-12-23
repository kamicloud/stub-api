<?php

namespace YetAnotherGenerator;

use Illuminate\Http\Request;

abstract class BaseMessage
{
    use ValueHelper;

    /**
     * @var Request
     */
    protected $request;

    public function __construct(Request $request)
    {
        $this->request = $request;

        $data = $request->all();

        $attributeMap = $this->responseRules();

        $this->fromJson($data, $attributeMap);
    }

    public function getRequest()
    {
        return $this->request;
    }

    public function validateInput()
    {
        $this->validateAttributes($this->requestRules());
    }

    public function validateOutput()
    {
        $this->validateAttributes($this->responseRules());
    }

    public function getResponse()
    {
        $attributeMap = $this->responseRules();

        $data = [];

        foreach ($attributeMap as $attribute) {
            [$field] = $attribute;
            $data[$field] = $this->$field;
        }

        $response = [
            'status' => 0,
            'message' => 'success',
            'data' => $data,
        ];

        return $response;
    }

    abstract public function requestRules();

    abstract public function responseRules();

}
