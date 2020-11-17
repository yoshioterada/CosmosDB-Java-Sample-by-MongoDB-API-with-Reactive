package com.yoshio3;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeMongoResourceIT extends MongoResourceTest {

    // Execute the same tests but in native mode.
}