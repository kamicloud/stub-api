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
//        foreach ($attributeMap as $attribute) {
//            [$field, $dbField, $isModel, $isArray, $type, $isOptional, $isMutable, $isEnum] = $attribute;
//
//            $value = $data[$field] ?? null;
//
//            if ($isEnum) {
//                $this->$field = $type::transform($value);
//            } elseif ($isModel) {
//                if ($isArray) {
//                    $this->$field = $type::initFromModels($value);
//                } else {
//                    $this->$field = $type::initFromModel($value);
//                }
//            } else {
//                $this->$field = $this->parseScalar($value, $type);
//            }
//        }
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
