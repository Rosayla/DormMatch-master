package com.example.dormmatch.models.propriedade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormmatch.repository.propriedadeRepository

class propriedadeViewModel : ViewModel() {

        private val repository : propriedadeRepository
        private val _allPropriedade = MutableLiveData<List<Propriedade>>()
        val allPropriedade : LiveData<List<Propriedade>> = _allPropriedade

        init {
            repository = propriedadeRepository().getInstance()
            repository.loadPropriedade(_allPropriedade)

        }
    /*fun filterLoadCategory(category: Int) {
        repository.filterLoadCategory(category, _allPropriedade)
    }*/
    }