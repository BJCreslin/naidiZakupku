package ru.bjcreslin.naidizakupku.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementResponseDto
import ru.bjcreslin.naidizakupku.procurement.filter.ProcurementFilter
import ru.bjcreslin.naidizakupku.procurement.mapper.ProcurementMapper
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.procutement.repository.ProcurementRepository
import java.io.IOException
import java.util.Optional

@RestController
@RequestMapping("/rest/admin-ui/procurements")
class ProcurementResource(
    private val procurementRepository: ProcurementRepository,
    private val procurementMapper: ProcurementMapper,
    private val objectMapper: ObjectMapper
) {
    @GetMapping
    fun getAll(@ModelAttribute filter: ProcurementFilter, pageable: Pageable): PagedModel<ProcurementResponseDto> {
        val spec: Specification<Procurement> = filter.toSpecification()
        val procurements: Page<Procurement> = procurementRepository.findAll(spec, pageable)
        val procurementResponseDtoPage: Page<ProcurementResponseDto> = procurements.map(procurementMapper::toProcurementDto)
        return PagedModel(procurementResponseDtoPage)
    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ProcurementResponseDto {
        val procurementOptional: Optional<Procurement> = procurementRepository.findById(id)
        return procurementMapper.toProcurementDto(procurementOptional.orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `$id` not found")
        })
    }

    @GetMapping("/by-ids")
    fun getMany(@RequestParam ids: List<Long>): List<ProcurementResponseDto> {
        val procurements: List<Procurement> = procurementRepository.findAllById(ids)
        return procurements.map(procurementMapper::toProcurementDto)
    }

    @PostMapping
    fun create(@RequestBody dto: ProcurementResponseDto): ProcurementResponseDto {
        val procurement: Procurement = procurementMapper.toEntity(dto)
        val resultProcurement: Procurement = procurementRepository.save(procurement)
        return procurementMapper.toProcurementDto(resultProcurement)
    }

    @PatchMapping("/{id}")
    @Throws(IOException::class)
    fun patch(@PathVariable id: Long, @RequestBody patchNode: JsonNode): ProcurementResponseDto {
        val procurement: Procurement = procurementRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `$id` not found")
        }
        val procurementDto = procurementMapper.toProcurementDto(procurement)
        objectMapper.readerForUpdating(procurementDto).readValue<ProcurementResponseDto>(patchNode)
        procurementMapper.updateWithNull(procurementDto, procurement)
        val resultProcurement: Procurement = procurementRepository.save(procurement)
        return procurementMapper.toProcurementDto(resultProcurement)
    }

    @PatchMapping
    @Throws(IOException::class)
    fun patchMany(@RequestParam ids: List<Long>, @RequestBody patchNode: JsonNode): List<Long> {
        val procurements: Collection<Procurement> = procurementRepository.findAllById(ids)
        for (procurement in procurements) {
            val procurementDto = procurementMapper.toProcurementDto(procurement)
            objectMapper.readerForUpdating(procurementDto).readValue<ProcurementResponseDto>(patchNode)
            procurementMapper.updateWithNull(procurementDto, procurement)
        }
        val resultProcurements: List<Procurement> = procurementRepository.saveAll(procurements)
        return resultProcurements.map(Procurement::id)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ProcurementResponseDto? {
        val procurement: Procurement? = procurementRepository.findById(id).orElse(null)
        if (procurement != null) {
            procurementRepository.delete(procurement)
        }
        return procurement?.let(procurementMapper::toProcurementDto)
    }

    @DeleteMapping
    fun deleteMany(@RequestParam ids: List<Long>) {
        procurementRepository.deleteAllById(ids)
    }
}
