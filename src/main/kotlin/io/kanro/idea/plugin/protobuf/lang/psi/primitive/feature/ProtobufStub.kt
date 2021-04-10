package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufStub<TStub : StubElement<TPsi>, TPsi : ProtobufElement> : StubBasedPsiElement<TStub>
