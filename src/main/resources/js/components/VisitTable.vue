<template>
    <v-container class="pa-2">
        <v-row>
            <v-col cols="8">
                <v-data-table :items="visits"
                              class="elevation-2"
                              :headers="headers"
                              item-key="id" :footer-props="{itemsPerPageText: 'visits'}">
                    <template #item.actions="{ item }">
                        <v-icon small @click="startEdit(item)" color="green">
                            {{plusIcon}}
                        </v-icon>
                        <v-icon small color="red">
                            {{deleteIcon}}
                        </v-icon>
                    </template>
                    <template v-slot:top>
                        <v-dialog v-model="isDialogActivated" max-width="500">
                            <template v-slot:activator="{ on, attrs }">
                                <div class="pa-3">
                                    <v-btn color="primary"
                                           dark
                                           class="pa-2"
                                           v-bind="attrs"
                                           v-on="on"
                                    >
                                        New Visit
                                    </v-btn>
                                </div>
                            </template>
                            <v-card>
                                <v-card-title>{{title}}</v-card-title>
                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedVisit.name" label="Patient name">
                                                </v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedVisit.secondName"
                                                              label="Patient second name">
                                                </v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedVisit.surname"
                                                              label="Patient surname">
                                                </v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <DatePicker label="Birthday"
                                                            v-model="editedVisit.birthday"></DatePicker>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <DatePicker label="Date out" v-model="editedVisit.dateOut"></DatePicker>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>
                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn text color="red" @click="close">Cancel</v-btn>
                                    <v-btn text color="green"
                                           :disabled="!allFieldsAreFilled"
                                           @click="save">Save
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </template>
                </v-data-table>
            </v-col>
            <v-col cols="4">
                <FilteringCard :update-visits="updateVisits"></FilteringCard>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import {mdiDelete, mdiPencil} from "@mdi/js";
    import Vue from "vue";
    import DatePicker from "./DatePicker.vue";
    import FilteringCard from "./FilteringCard.vue";

    export default {
        name: "VisitTable",
        components: {FilteringCard, DatePicker},
        data() {
            return {
                isDialogActivated: false,
                plusIcon: mdiPencil,
                deleteIcon: mdiDelete,
                editedIndex: -1,
                editedVisit: {
                    id: null,
                    name: '',
                    secondName: '',
                    surname: '',
                    birthday: '',
                    dateOut: ''
                },
                headers: [
                    {text: "Name", value: "name"},
                    {text: "SecondName", value: "secondName"},
                    {text: "Surname", value: "surname"},
                    {text: "Birthday", value: "birthday"},
                    {text: "Dateout", value: "dateOut"},
                    {text: "Actions", value: "actions"}
                ],
                visits: []
            }
        },
        methods: {
            close() {
                this.isDialogActivated = false;
                this.editedIndex = -1,
                    this.editedVisit.id = null,
                    this.editedVisit.name = '',
                    this.editedVisit.secondName = '',
                    this.editedVisit.surname = '',
                    this.editedVisit.birthday = '',
                    this.editedVisit.dateOut = '';
            },
            startEdit(visit) {
                this.editedIndex = this.visits.indexOf(visit);
                Object.assign(this.editedVisit, visit);
                this.isDialogActivated = true
            },
            save() {
                if (this.allFieldsAreFilled) {
                    if (this.editedIndex !== -1) {
                        Vue.http.put("/api/visit/" + this.visits[this.editedIndex].id,
                            this.editedVisit)
                            .then(() =>
                                this.visits = [
                                    ...this.visits.slice(0, this.editedIndex),
                                    Object.assign({}, this.editedVisit),
                                    ...this.visits.slice(this.editedIndex + 1)
                                ])
                            .then(() => this.close())
                    } else {
                        Vue.http.post("/api/visit", this.editedVisit)
                            .then(() => this.visits.push(Object.assign({}, this.editedVisit)))
                            .then(() => this.close())
                    }
                }
            },
            updateVisits(visits) {
                this.visits = visits
            }
        },
        computed: {
            title() {
                return this.editedIndex === -1 ? "New visit" : "Edit visit"
            },
            allFieldsAreFilled() {
                return this.editedVisit.name !== ''
                    && this.editedVisit.surname !== ''
                    && this.editedVisit.secondName !== ''
                    && this.editedVisit.birthday !== ''
                    && this.editedVisit.dateOut !== ''
            }
        },
        watch: {
            isDialogActivated(val) {
                if (!val) {
                    this.close()
                }
            }
        },
        created() {
            Vue.http.get("/api/visit")
                .then(result => result.json())
                .then(result => result.forEach(v => this.visits.push(v)))
        }
    }

</script>

<style scoped>

</style>