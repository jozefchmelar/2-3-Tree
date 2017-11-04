package app.controller

import gui.model.Hospital
import gui.model.InsuranceCompany
import gui.model.Patient

class CurrentlyHospitalizedController : BaseController()
{

    fun getHospitalizations(hospital: Hospital){
        foundPatients.setAll(hospital.currentHospitalizations.getValuesInorder())
    }

    fun getHospitalizations(hospital: Hospital,insuranceCompany: InsuranceCompany){
        println(insuranceCompany)
        foundPatients.setAll(hospital.currentInsuranceHospitalizations.get(insuranceCompany) ?: emptyList<Patient>())
    }
}