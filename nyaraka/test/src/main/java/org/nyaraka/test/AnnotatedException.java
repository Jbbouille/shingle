package org.nyaraka.test;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiResponses({
        @ApiResponse(code = 507, message = "Pack was successfully purchased and credited to the user", response = Child.class),
        @ApiResponse(code = 508, message = "Pack was successfully purchased and credited to the user", response = Group.class)
})
public class AnnotatedException extends Exception {
}
