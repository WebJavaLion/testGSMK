<template>
    <v-card>
        <v-card-title>Filtering by field(s)</v-card-title>
        <v-card-text>
            <v-row>
                <v-col cols="12" sm="6" md="4">
                    <v-text-field dense v-model="fields.name" label="Patient name">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                    <v-text-field dense v-model="fields.secondName"
                                  label="Patient second name">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                    <v-text-field dense v-model="fields.surname"
                                  label="Patient surname">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                    <DatePicker label="Birthday" v-model="fields.birthday"></DatePicker>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                    <DatePicker label="Date out" v-model="fields.dateOut"></DatePicker>
                </v-col>
            </v-row>
            <v-card-actions>
                <v-btn @click="clearFields" color="red">clear</v-btn>
                <v-btn @click="updateByFields" color="primary">select</v-btn>
            </v-card-actions>
        </v-card-text>
    </v-card>
</template>

<script>
    import DatePicker from "./DatePicker.vue";
    import Vue from 'vue'

    export default {
        name: "FilteringCard",
        props: ['updateVisits'],
        components: {DatePicker},
        data() {
            return {
                fields: {
                    name: '',
                    secondName: '',
                    surname: '',
                    birthday: '',
                    dateOut: ''
                }
            }
        },
        methods: {
            updateByFields() {
                let entries = Object.entries(this.fields).filter(e => e[1] !== '');
                let params = Object.fromEntries(entries);
                Vue.http.get("/api/visit/",
                    {
                        params: params
                    })
                    .then(res => res.json())
                    .then(res => this.updateVisits(res))
            },
            clearFields() {
                    this.fields.name = '',
                    this.fields.secondName = '',
                    this.fields.surname = '',
                    this.fields.birthday = '',
                    this.fields.dateOut = ''
            }
        }
    }
</script>

<style scoped>

</style>