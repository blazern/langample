package blazern.langample.data.panlex

import arrow.core.Either
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.panlex.model.WordData
import blazern.langample.data.panlex.model.api.DenotationRequest
import blazern.langample.data.panlex.model.api.DenotationResponse
import blazern.langample.data.panlex.model.api.ExprRequest
import blazern.langample.data.panlex.model.api.ExprResponse
import blazern.langample.data.panlex.model.api.LangvarResponse
import blazern.langample.domain.model.Lang
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PanLexClient(
    private val ktorClientHolder: KtorClientHolder,
) {
    private val langUidMap = Lang.entries.associateBy { it.uid }

    suspend fun search(
        query: String,
        langFrom: Lang,
        langsTo: List<Lang>,
    ): Either<Exception, WordData> {
        return try {
            Either.Right(queryForWordData(query, langFrom, langsTo))
        } catch (e: Exception) {
            Either.Left(e)
        }
    }

    private suspend fun queryForWordData(
        query: String,
        langFrom: Lang,
        langsTo: List<Lang>,
    ): WordData {
        var wordData = WordData(
            word = query,
            langFrom,
        )

        val langvarResp: LangvarResponse = ktorClientHolder.client.post(LANGVAR_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "uid" to listOf(langFrom.uid) + langsTo.map { it.uid },
                )
            )
        }.body()

        val langvarMap = langvarResp.result.associate {
            val lang = langUidMap[it.uid]
            if (lang == null) {
                throw IllegalStateException("Could not get a Lang for $it")
            } else {
                it.id to lang
            }
        }

        val translationsIdsResp: ExprResponse = ktorClientHolder.client.post(EXPR_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                ExprRequest(
                    uid = langFrom.uid,
                    txt = query,
                    transUids = langsTo.map { it.uid },
                    limit = EXPRESSIONS_LIMIT,
                )
            )
        }.body()

        val translationsResp: ExprResponse = ktorClientHolder.client.post(EXPR_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "id" to translationsIdsResp.result.map { it.transExpr },
                )
            )
        }.body()
        wordData = wordData.copy(
            translations = translationsResp.result.map {
                val lang = langvarMap[it.langvar]
                    ?: throw IllegalStateException("Could not get a Lang for $it")
                WordData(it.txt, lang)
            }
        )

        if (translationsIdsResp.result.isNotEmpty()) {
            val denotationResp: DenotationResponse = ktorClientHolder.client.post(DENOTATION_URL) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "expr" to translationsIdsResp.result[0].id,
                    )
                )
            }.body()

            val meaningsResp: DenotationResponse = ktorClientHolder.client.post(DENOTATION_URL) {
                contentType(ContentType.Application.Json)
                setBody(
                    DenotationRequest(
                        uid = langFrom.uid,
                        meaning = denotationResp.result.map { it.meaning },
                    )
                )
            }.body()

            val expressionsResp: ExprResponse = ktorClientHolder.client.post(EXPR_URL) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "id" to meaningsResp.result.map { it.expr },
                    )
                )
            }.body()

            wordData = wordData.copy(
                synonyms = expressionsResp.result.map {
                    val lang = langvarMap[it.langvar]
                        ?: throw IllegalStateException("Could not get a Lang for $it")
                    WordData(it.txt, lang)
                }
            )
        }
        return wordData
    }

    companion object {
        private const val EXPR_URL = "https://api.panlex.org/v2/expr"
        private const val LANGVAR_URL = "https://api.panlex.org/v2/langvar"
        private const val DENOTATION_URL = "https://api.panlex.org/v2/denotation"
        private const val EXPRESSIONS_LIMIT = 20
    }
}

val Lang.uid: String
    get() = when (this) {
        Lang.RU -> "rus-000"
        Lang.EN -> "eng-000"
        Lang.DE -> "deu-000"
    }


// # Get translations of "Imker":
//curl -s -X POST https://api.panlex.org/v2/expr \
//     -H 'Content-Type: application/json' \
//     -d '{
//           "uid":"deu-000",
//           "txt":"Imker",
//           "trans_uid":["eng-000","rus-000"],
//           "limit":20
//         }' > /tmp/step1.json \
//&& TX_IDS=$(jq '[.result[].trans_expr] | unique' /tmp/step1.json) \
//&& curl -s -X POST https://api.panlex.org/v2/expr \
//     -H 'Content-Type: application/json' \
//     -d "$(jq -n --argjson ids "$TX_IDS" \
//               '{id:$ids}')"
//
//# Get "langvar" lang_codes:
//curl -X POST https://api.panlex.org/v2/langvar \
//     -H 'Content-Type: application/json' \
//     -d '{ "uid":["eng-000","rus-000","ukr-000"] }'
//{"resultType":"langvar","result":[{"grp":187,"id":187,"lang_code":"eng","meaning":35686799,"mutable":true,"name_expr":11611,"name_expr_txt":"English","name_expr_txt_degr":"english","region_expr":26528845,"script_expr":18147719,"uid":"eng-000","var_code":0},{"grp":620,"id":620,"lang_code":"rus","meaning":35676635,"mutable":true,"name_expr":43116,"name_expr_txt":"русский","name_expr_txt_degr":"русскии","region_expr":26528845,"script_expr":17807488,"uid":"rus-000","var_code":0},{"grp":755,"id":755,"lang_code":"ukr","meaning":35676767,"mutable":true,"name_expr":45720,"name_expr_txt":"українська","name_expr_txt_degr":"украінська","region_expr":26528845,"script_expr":17807488,"uid":"ukr-000","var_code":0}],"resultNum":3,"resultMax":2000}
//
//# Get synonyms:
//curl -s -X POST https://api.panlex.org/v2/expr \
//     -H 'Content-Type: application/json' \
//     -d '{ "uid":"deu-000", "txt":"Imker", "limit":1 }' > /tmp/e.json \
//&& EID=$(jq '.result[0].id' /tmp/e.json) \
//&& curl -s -X POST https://api.panlex.org/v2/denotation \
//     -H 'Content-Type: application/json' \
//     -d "{ \"expr\": $EID }" > /tmp/m.json \
//&& MEANINGS=$(jq -c '[.result[].meaning] | unique' /tmp/m.json) \
//&& curl -s -X POST https://api.panlex.org/v2/denotation \
//     -H 'Content-Type: application/json' \
//     -d "$(jq -c -n --argjson m "$MEANINGS" \
//               '{meaning:$m, uid:["deu-000"]}')" > /tmp/d.json \
//&& EXPRS=$(jq -c "[.result[].expr | select(. != $EID)] | unique" /tmp/d.json) \
//&& curl -s -X POST https://api.panlex.org/v2/expr \
//     -H 'Content-Type: application/json' \
//     -d "$(jq -n --argjson ids "$EXPRS" '{id:$ids}')" \
//| jq -r '.result[].txt' | sort -u