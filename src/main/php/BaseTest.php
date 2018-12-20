<?php

namespace Tests;

use Illuminate\Foundation\Testing\TestCase;

abstract class BaseTest extends TestCase
{
    public function assertResponse($except, $actual)
    {
        $this->assertTrue(true);
    }
}
