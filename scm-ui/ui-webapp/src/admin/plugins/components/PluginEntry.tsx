/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { Link, Plugin } from "@scm-manager/ui-types";
import { CardColumn, Icon } from "@scm-manager/ui-components";
import PluginAvatar from "./PluginAvatar";
import { PluginAction, PluginModalContent } from "../containers/PluginsOverview";

type Props = {
  plugin: Plugin;
  openModal: (content: PluginModalContent) => void;
};

const ActionbarWrapper = styled.div`
  & span + span {
    margin-left: 0.5rem;
  }
`;

const IconWrapper = styled.span`
  margin-bottom: 0 !important;
  padding: 0.5rem;
  border: 1px solid #cdcdcd; // $dark-25
  border-radius: 4px;
  cursor: pointer;
  pointer-events: all;

  &:hover {
    border-color: #9a9a9a; // $dark-50
  }
`;

const PluginEntry: FC<Props> = ({ plugin, openModal }) => {
  const [t] = useTranslation("admin");
  const isInstallable = plugin._links.install && (plugin._links.install as Link).href;
  const isUpdatable = plugin._links.update && (plugin._links.update as Link).href;
  const isUninstallable = plugin._links.uninstall && (plugin._links.uninstall as Link).href;

  const pendingSpinner = () => (
    <Icon className="fa-spin fa-lg" name="spinner" color={plugin.markedForUninstall ? "danger" : "info"} />
  );
  const actionBar = () => (
    <ActionbarWrapper className="is-flex">
      {isInstallable && (
        <IconWrapper className="level-item" onClick={() => openModal({ plugin, action: PluginAction.INSTALL })}>
          <Icon title={t("plugins.modal.install")} name="download" color="info" />
        </IconWrapper>
      )}
      {isUninstallable && (
        <IconWrapper className="level-item" onClick={() => openModal({ plugin, action: PluginAction.UNINSTALL })}>
          <Icon title={t("plugins.modal.uninstall")} name="trash" color="info" />
        </IconWrapper>
      )}
      {isUpdatable && (
        <IconWrapper className="level-item" onClick={() => openModal({ plugin, action: PluginAction.UPDATE })}>
          <Icon title={t("plugins.modal.update")} name="sync-alt" color="info" />
        </IconWrapper>
      )}
    </ActionbarWrapper>
  );

  return (
    <>
      <CardColumn
        action={isInstallable ? () => openModal({ plugin, action: PluginAction.INSTALL }) : undefined}
        avatar={<PluginAvatar plugin={plugin} />}
        title={plugin.displayName ? <strong>{plugin.displayName}</strong> : <strong>{plugin.name}</strong>}
        description={plugin.description}
        contentRight={plugin.pending || plugin.markedForUninstall ? pendingSpinner() : actionBar()}
        footerLeft={<small>{plugin.version}</small>}
        footerRight={<small className="level-item is-block shorten-text">{plugin.author}</small>}
      />
    </>
  );
};

export default PluginEntry;
