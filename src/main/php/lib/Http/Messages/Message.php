<?php

namespace YetAnotherGenerator\Http\Messages;

use Illuminate\Http\Request;
use YetAnotherGenerator\Concerns\ValueHelper;

abstract class Message
{
    use ValueHelper;

    /**
     * @var Request
     */
    protected $request;
    protected $fileResponse;

    public function __construct(Request $request)
    {
        $this->request = $request;

        $data = $request->all();

        $attributeMap = $this->requestRules();

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

    public function setFileResponse($fileResponse)
    {
        $this->fileResponse = $fileResponse;
    }

    public function getFileResponse()
    {
        return $this->fileResponse;
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
            config('generator.keys.status', 'status') => config('generator.values.success-status', 0),
            config('generator.keys.message', 'message') => config('generator.values.success-message', 'success'),
            config('generator.keys.data', 'data') => $data,
        ];

        return $response;
    }

    abstract public function requestRules();

    abstract public function responseRules();

}
