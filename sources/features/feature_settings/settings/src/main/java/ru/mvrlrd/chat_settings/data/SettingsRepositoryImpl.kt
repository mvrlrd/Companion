package ru.mvrlrd.chat_settings.data

import ru.mvrlrd.chat_settings.domain.ChatSettings
import ru.mvrlrd.chat_settings.domain.SettingsRepository
import ru.mvrlrd.core_api.database.chat.ChatDao
import javax.inject.Inject

class SettingsRepositoryImpl@Inject constructor(private val chatDao: ChatDao, private val mapper: SettingsMapper) : SettingsRepository{
    override suspend fun saveSettings(chatSettings: ChatSettings) {
        val chatEntity = mapper.mapSettingsToChat(chatSettings)
        chatDao.upsertChat(chatEntity)
    }
}