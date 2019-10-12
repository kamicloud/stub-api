<?php

namespace Kamicloud\StubApi\Http\Messages;

use Illuminate\Http\Request;
use Kamicloud\StubApi\Concerns\ValueHelper;

abstract class Message
{
    use ValueHelper;

    /**
     * @var Request
     */
    protected $request;
    protected $fileResponse;
    protected $errorResponse;

    public function __construct(Request $request)
    {
        $this->request = $request;

        $data = $request->all();

        $data = array_merge($data, $request->json()->all());

        $attributeMap = $this->requestRules();

        $this->fromJson($data, $attributeMap);
    }

    public function getRequest()
    {
        return $this->request;
    }

    public function validateInput()
    {
        $this->validateAttributes($this->requestRules(), "request");
    }

    public function validateOutput()
    {
        if (!$this->errorResponse) {
            $this->validateAttributes($this->responseRules(), "response", false);
        }
    }

    public function setErrorResponse($errorResponse)
    {
        $this->errorResponse = $errorResponse;
    }

    public function getErrorResponse()
    {
        return $this->errorResponse;
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
        if ($this->errorResponse) {
            return $this->errorResponse;
        }
        $attributeMap = $this->responseRules();

        $data = $this->mutableAttributes($attributeMap);

        $response = [
            config('generator.keys.status', 'status') => config('generator.values.success-status', 0),
            config('generator.keys.message', 'message') => config('generator.values.success-message', 'success'),
            config('generator.keys.data', 'data') => $data ?: null,
        ];

        return $response;
    }

    abstract public function requestRules();

    abstract public function responseRules();

}
