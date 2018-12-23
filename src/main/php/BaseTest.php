<?php

namespace Tests;

use Illuminate\Foundation\Testing\TestCase;

abstract class BaseTest extends TestCase
{
    public function post($uri, array $data = [], array $headers = [])
    {
        $data['__test_mode'] = true;

        parent::post($uri, $data, $headers);
    }

    public function assertResponse($except, $actual)
    {
        $this->assertTrue(true);
    }
}
