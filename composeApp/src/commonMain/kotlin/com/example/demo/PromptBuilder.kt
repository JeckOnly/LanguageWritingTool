package com.example.demo

import com.example.demo.data.repo.CheckMode

object PromptBuilder {

    fun buildSystemPrompt(mode: CheckMode): String {
        val style = when (mode) {
            CheckMode.RewriteNatural -> "more natural, idiomatic, and fluent"
            CheckMode.RewriteFormal  -> "more formal and professional"
            CheckMode.RewriteConcise -> "more concise while keeping meaning"
        }

        return """
You are an English writing assistant.

Task:
Rewrite ONLY the user's DRAFT text to be $style.

Important:
The DRAFT may contain BOTH English and Chinese.
Treat any Chinese parts as MEANING HINTS, not text to translate word-by-word.

Process (internal, do not explain):
1. Understand the full meaning of the sentence as a whole.
2. Replace any Chinese parts with appropriate, natural English that fits the context.
3. Then polish the entire sentence for grammar, word choice, collocation, and fluency.

Use CONTEXT only for tone and consistency. Do NOT modify or rewrite CONTEXT.

Output requirements:
- Output must be in English ONLY.
- Return ONLY valid JSON (no markdown, no extra text).
- Do NOT explain your reasoning.

JSON format:
{
  "rewritten": "...",
  "alternatives": ["...", "...", "..."],
}

Rules:
- Preserve the original meaning.
- Do not add new information.
- If the draft is already good, make minimal changes.
- Alternatives should express the same meaning with different wording or tone.
        """.trimIndent()
    }

    fun buildUserContent(contextText: String, draftText: String): String {
        return """
CONTEXT (already written, for reference only):
"${contextText.trim()}"

DRAFT (may contain Chinese, rewrite into proper English):
"${draftText.trim()}"
        """.trimIndent()
    }
}
