package com.alejandroestevemaza.gymtimerpro.design

import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState
import org.junit.Assert.assertEquals
import org.junit.Test

class ComponentStateContractTest {
    @Test
    fun component_state_matrix_contains_required_states() {
        val expected = setOf(
            GymComponentState.Normal,
            GymComponentState.Pressed,
            GymComponentState.Disabled,
            GymComponentState.Loading,
            GymComponentState.Error,
        )
        assertEquals(expected, GymComponentState.entries.toSet())
    }
}
