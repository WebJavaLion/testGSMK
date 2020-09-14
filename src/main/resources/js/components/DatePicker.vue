<template>
    <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="200px"
            min-width="200px"
    >
        <template v-slot:activator="{on, attrs}">
            <v-text-field dense
                          v-model="value"
                          :label="label"
                          v-bind="attrs"
                          v-on="on">
            </v-text-field>
        </template>
        <v-date-picker
                width="200"
                v-model="current"
                @input="$emit('input', $event)"
                type="date"
                no-title
                scrollable
        >
        </v-date-picker>
    </v-menu>
</template>

<script>
    export default {
        props: ['value', 'label'],
        name: "DatePicker",

        data() {
            return {
                menu: false,
                current: this.value
            }
        },
    }
</script>

<style scoped>

</style>